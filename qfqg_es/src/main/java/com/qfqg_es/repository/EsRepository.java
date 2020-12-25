package com.qfqg_es.repository;

import com.qfqg_es.model.EsFile;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EsRepository extends ElasticsearchRepository<EsFile,String> {

}
