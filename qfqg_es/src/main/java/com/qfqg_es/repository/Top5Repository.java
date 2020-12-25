package com.qfqg_es.repository;

import com.qfqg_es.model.TopK;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface Top5Repository extends CrudRepository<TopK,Long>{
//    @Query("select * from TopK order by searchedHits desc ,'limit' 5")
//    List<TopK> getTop5Keywords();
}
