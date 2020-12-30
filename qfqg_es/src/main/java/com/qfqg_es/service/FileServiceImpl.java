package com.qfqg_es.service;

import com.qfqg_es.model.EsFile;
import com.qfqg_es.repository.EsRepository;
import com.qfqg_es.response.FileResponse;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;

import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
public class FileServiceImpl implements FileService {

    @Autowired
    private EsRepository repo;
    @Autowired
    private ElasticsearchRestTemplate template;

    private static final Logger logger = LoggerFactory.getLogger(FileServiceImpl.class);

    @Qualifier("highLevelClient")
    @Autowired
    private RestHighLevelClient client;
    @Override
    public String addAll() {
        return null;
    }

    @Override
    public String add(EsFile esFile) {
        if(esFile==null){
            return "File is empty";
        }
        repo.save(esFile);
        return "Successfully added";
    }

    @Override
    public FileResponse highLightSearch(String keyword, Integer pageNum){
        NativeSearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders.multiMatchQuery(keyword, "fileName", "fileContent"))
                         //以修改时间排序（最近的在最前）
                         //.withSort(SortBuilders.fieldSort("fileModifiedDate").order(SortOrder.DESC))
                         //.withSort(SortBuilders.fieldSort("fileSearchScore").order(SortOrder.DESC))
                         .withPageable(PageRequest.of(pageNum, 6))
                         .withHighlightFields(
                                         new HighlightBuilder.Field("fileName").preTags("<em>").postTags("</em>"),
                                         new HighlightBuilder.Field("fileContent").preTags("<em>").postTags("</em>")//.fragmentSize(6)
                         ).build();
        SearchHits<EsFile> search = template.search(searchQuery, EsFile.class);
        // 得到查询结果返回的内容
        List<SearchHit<EsFile>> searchHits = search.getSearchHits();
        // 设置一个需要返回的实体类集合
        List<EsFile> fileList = new ArrayList<>();
        // 遍历返回的内容进行处理
        for(SearchHit<EsFile> searchHit : searchHits){
            // 高亮的内容
            Map<String, List<String>> highLightFields = searchHit.getHighlightFields();
            //设置高亮内容
            searchHit = setHighLightFields(searchHit,highLightFields);
            // 放到实体类中
            //logger.info();
            //searchHit.getContent().setFileSearchScore(searchHit.getScore());
            fileList.add(searchHit.getContent());
        }
        FileResponse result = new FileResponse();
        result.setData(fileList);
        long hits = search.getTotalHits();
        logger.info("搜索结果数量："+hits);
        result.setHits(hits);
        //result.setSearchTime();
        return result;
    }
    //添加高亮文本
    private SearchHit setHighLightFields(SearchHit<EsFile> searchHit
            ,Map<String, List<String>> highLightFields){
        searchHit.getContent().setFileName(highLightFields.get("fileName") == null ? searchHit.getContent().getFileName() : highLightFields.get("fileName").get(0));
        if(highLightFields.get("fileContent")!=null&&searchHit.getContent().getFileType().equals("lua")){
            searchHit.getContent().setHighLightFields("");
            for(String field:highLightFields.get("fileContent")){
                field = field.replaceAll("\r|\n| ", "");
                searchHit.getContent().setHighLightFields(
                        searchHit.getContent().getHighLightFields()+field);
            }
        } else if(highLightFields.get("fileContent")!=null&&searchHit.getContent().getFileType().equals("xml")){
            searchHit.getContent().setHighLightFields("");
            for(String field:highLightFields.get("fileContent")){
                field = field.replaceAll("\r|\n| ", "");
                searchHit.getContent().setHighLightFields(
                        searchHit.getContent().getHighLightFields()+field);
            }
            //转义xml标签
            String highLight = searchHit.getContent().getHighLightFields();
            highLight = highLight.replaceAll("<","&lt;");
            highLight = highLight.replaceAll(">","&gt;");
            highLight = highLight.replaceAll("&lt;em&gt;","<em>");
            highLight = highLight.replaceAll("&lt;/em&gt;","</em>");
            searchHit.getContent().setHighLightFields(highLight);
        }else {
            searchHit.getContent().setHighLightFields(
                    searchHit.getContent().getFileContent());
        }
        return searchHit;
    }
    /*
    * 创建索引
    * */
    public boolean createFileIndex(String indexName) {
        // 判断索引是否存在，存在就返回true
        if (isIndexExist(indexName)) return true;
        // 创建索引请求
        CreateIndexRequest indexRequest = new CreateIndexRequest(indexName);
        // 可选参数，备份2，碎片3
        indexRequest.settings(Settings.builder()
                .put("index.number_of_shards", 3)
                .put("index.number_of_replicas", 2));
        // 可选参数，超时时间
        indexRequest.setTimeout(TimeValue.timeValueSeconds(2));
        
        // 字段和类型，可选
        indexRequest.mapping(
                "{\n" +
                        "  \"properties\": {\n" +
                        "    \"FileName\": {\n" +
                        "      \"type\": \"text\",\n" +
                        "      \"analyzer\": \"ik_max_word\",\n"+
                        "       \"search_analyzer\": \"ik_smart\",\n"+
                        "      \"fields\": {\n"+
                        "         \"keyword\" : {\n"+
                        "           \"type\" :\"keyword\",\n"+
                        "           \"ignore_above\" : 256\n"+
                        "         }\n"+
                        "       }\n"+
                        "    },\n" +
                        "    \"FileUrl\": {\n" +
                        "      \"type\": \"text\",\n" +
                        "      \"fields\": {\n"+
                        "         \"keyword\" : {\n"+
                        "           \"type\" :\"keyword\",\n"+
                        "           \"ignore_above\" : 256\n"+
                        "         }\n"+
                        "       }\n" +
                        "    },\n" +
                        "    \"FileType\": {\n" +
                        "      \"type\": \"text\",\n" +
                        "      \"fields\": {\n"+
                        "         \"keyword\" : {\n"+
                        "           \"type\" :\"keyword\",\n"+
                        "           \"ignore_above\" : 256\n"+
                        "         }\n"+
                        "       }\n" +
                        "    },\n" +
//                        "    \"FileSearchScore\": {\n" +
//                        "      \"type\": \"double\",\n" +
//                        "      \"fields\": {\n"+
//                        "         \"keyword\" : {\n"+
//                        "           \"type\" :\"keyword\",\n"+
//                        "           \"ignore_above\" : 256\n"+
//                        "         }\n"+
//                        "       }\n"+
//                        "    },\n" +
                        "    \"FileModifiedDate\": {\n" +
                        "      \"type\": \"date\",\n" +
                        "      \"fields\": {\n"+
                        "         \"keyword\" : {\n"+
                        "           \"type\" :\"keyword\",\n"+
                        "           \"ignore_above\" : 256\n"+
                        "         }\n"+
                        "       }\n"+
                        "    },\n" +
                        "    \"FileContent\": {\n" +
                        "      \"type\": \"text\",\n" +
                        "      \"analyzer\": \"ik_max_word\",\n"+
                        "       \"search_analyzer\": \"ik_smart\",\n"+
                        "      \"fields\": {\n"+
                        "         \"keyword\" : {\n"+
                        "           \"type\" :\"keyword\",\n"+
                        "           \"ignore_above\" : 256\n"+
                        "         }\n"+
                        "       }\n"+
                        "    },\n" +
                        "     \"FileStrategyName\": {\n" +
                        "      \"type\": \"text\",\n" +
                        "      \"fields\": {\n"+
                        "         \"keyword\" : {\n"+
                        "           \"type\" :\"keyword\",\n"+
                        "           \"ignore_above\" : 256\n"+
                        "         }\n"+
                        "       }\n"+
                        "     }\n" +
                        "    }\n" +
                        "  }\n" +
                        "}",
                XContentType.JSON);
        try {
            // 同步方式
            client.indices().create(indexRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            System.out.println("---创建ES索引失败---");
            e.printStackTrace();
        }
        return true;
    }
    private boolean isIndexExist(String index) {
        GetIndexRequest request = new GetIndexRequest(index);
        try {
            boolean exists = client.indices().exists(request, RequestOptions.DEFAULT);
            if (exists) return true;
        } catch (IOException e) {
            System.out.println("===判断索引是否存在请求失败");
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 删除索引
     * @param indexName
     */
    private void deleteIndex(String indexName) {
        DeleteIndexRequest request = new DeleteIndexRequest(indexName);
        request.timeout("2m");
        try {
            client.indices().delete(request, RequestOptions.DEFAULT);
        } catch (IOException e) {
            System.out.println("===删除索引失败===");
            e.printStackTrace();
        }
    }

}
