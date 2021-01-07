package com.qfqg_es.service;

import com.qfqg_es.helper.CacheManager;
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
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import com.qfqg_es.helper.Cache;
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
    private CacheManager cacheManager;
    @Autowired
    private ElasticsearchRestTemplate template;
    private static final Logger logger = LoggerFactory.getLogger(FileServiceImpl.class);
    //缓存的唯一主键
    private static final String cacheKey = "esHighlightSearch";

    @Qualifier("highLevelClient")
    @Autowired
    private RestHighLevelClient client;

    @Override
    public String addAll() {
        return null;
    }

    /**
     * 向Es中插入单个文件
     * @param esFile 对象
     * */
    @Override
    public String add(EsFile esFile) {
        if(esFile==null){
            return "File is empty";
        }
        repo.save(esFile);
        return "Successfully added";
    }

    /**
     * 通过文件id获取文件详细信息
     * @param id 文件id
     * */
    public EsFile getFileDetails(String id){
        //获取缓存
        Cache cache = cacheManager.getCacheInfo(FileServiceImpl.cacheKey);
        if(cache == null){
            //若缓存为空，则到es中查询
            logger.info("========== 缓存失效，通过es查询文件内容 ==========");
            return repo.findById(id).get();
        } else {
            //若缓存不为空，则在缓存中查询文件信息
            logger.info("========== 从缓存中载入文件内容 ==========");
            return getFileFromCache(id);
        }
    }

    /**
     * 高亮分页搜索
     * @param keyword 关键字
     * @param pageNum 第几页
     * */
    @Override
    public FileResponse highLightSearch(String keyword, Integer pageNum){
        //每次搜索之前清除缓存
        cacheManager.clearAll();
        //创建搜索用的Query
        NativeSearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders.matchQuery("fileContent", keyword))
                         //以修改时间排序（最近的在最前）
                         //.withSort(SortBuilders.fieldSort("fileModifiedDate").order(SortOrder.DESC))
                         //.withSort(SortBuilders.fieldSort("fileSearchScore").order(SortOrder.DESC))
                         .withPageable(PageRequest.of(pageNum, 6))
                         .withHighlightFields(
                                 new HighlightBuilder.Field("fileContent").preTags("<em>").postTags("</em>")//.fragmentSize(6)
                         ).build();
        //使用ElasticSearchRestTemplate进行搜索
        logger.info("========== 访问ElasticSearch ==========");
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
            //searchHit.getContent().setFileSearchScore(searchHit.getScore());
            fileList.add(searchHit.getContent());
        }
        //定义一个response实例并将搜索到的文件添加到实例当中
        FileResponse result = new FileResponse();
        result.setData(fileList);
        //将搜索到的文件写入缓存
        logger.info("========== 搜索结束，搜索结果写入缓存 ==========");
        cacheFile(fileList);
        //获取搜索到的总结果数量 并添加到response实例中
        long hits = search.getTotalHits();
        logger.info("搜索结果数量："+hits);
        result.setHits(hits);
        return result;
    }
    /**
     * 添加高亮文本
     * */
    private SearchHit setHighLightFields(SearchHit<EsFile> searchHit
            ,Map<String, List<String>> highLightFields){

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
    /**
    * 创建索引
    * */
    public boolean createFileIndex(String indexName) {
        // 判断索引是否存在，存在就返回true
        if (isIndexExist(indexName)) return true;
        logger.info("===== 创建新索引 =====");
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
                        //"      \"analyzer\": \"ik_max_word\",\n"+
                        //"       \"search_analyzer\": \"ik_smart\",\n"+
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
        logger.info("===== 索引创建完毕 =====");
        return true;
    }
    /**
     * 检查给定索引是否已存在
     * */
    public boolean isIndexExist(String index) {
        GetIndexRequest request = new GetIndexRequest(index);
        try {
            boolean exists = client.indices().exists(request, RequestOptions.DEFAULT);
            if (exists) return true;
        } catch (IOException e) {
            System.out.println("===判断索引是否存在请求失败===");
            e.printStackTrace();
        }
        return false;
    }
    /**
     * 判断索引是否为jpa自动创建
     * */
    public boolean isAutoCreated(String index) {
        GetIndexRequest request = new GetIndexRequest(index);
        try {
            String setting = client.indices().get(request, RequestOptions.DEFAULT)
                    .getSetting(index,"index.number_of_shards");
            if(setting.equals("1")) return true;

        } catch (IOException e) {
            System.out.println("===判断失败===");
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 删除索引
     * @param indexName
     */
    public void deleteIndex(String indexName) {
        DeleteIndexRequest request = new DeleteIndexRequest(indexName);
        request.timeout("2m");
        try {
            client.indices().delete(request, RequestOptions.DEFAULT);
        } catch (IOException e) {
            System.out.println("===删除索引失败===");
            e.printStackTrace();
        }
        logger.info("===== JPA默认索引已删除 =====");
    }

    /**
    * 将文件写入缓存
    * */
    private void cacheFile(List<EsFile> list){
        String key = FileServiceImpl.cacheKey;
        Cache cache= cacheManager.getCacheInfo(key);
        if(cache == null){
            cache = new Cache();
            cache.setKey(key);
            cache.setValue(list);
            cacheManager.putCache(key, cache);
        } else {
            logger.warn("esHighlightSearch 未清理");
        }
    }
    /**
    * 从缓存中加载文件
    * */
    private EsFile getFileFromCache(String id){
        Cache cache = cacheManager.getCacheInfo(FileServiceImpl.cacheKey);
        List<EsFile> list = (List)cache.getValue();
        for(EsFile file:list){
            if(file.getId().equals(id)){
                return file;
            }
        }
        return null;
    }

}
