package com.qfqg_es.repository;

import com.qfqg_es.model.Keyword;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface Top5Repository extends CrudRepository<Keyword,Long>{
      @Query(value = "SELECT i FROM Keyword i ORDER BY i.searchedHits DESC, i.lastDate DESC")
      Optional<List<Keyword>> findAllOrderByHitsAndDate();
      Optional<Keyword> findByKeywordName(String keywordName);
      boolean existsByKeywordName(String KeywordName);
}
