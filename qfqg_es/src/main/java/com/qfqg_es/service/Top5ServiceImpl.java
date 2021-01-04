package com.qfqg_es.service;

import com.qfqg_es.model.Keyword;
import com.qfqg_es.repository.Top5Repository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class Top5ServiceImpl implements Top5Service {
    @Autowired
    private Top5Repository repo;

    @Override
    public List<Keyword> top5Search() {

        return null;
    }

    @Override
    public Keyword updateTopK(String keyword) {
        Keyword newKey = repo.findByKeywordName(keyword).get();
        if(newKey == null){
            newKey = new Keyword();
            newKey.setKeywordName(keyword);
            newKey.setSearchedHits(1);
            newKey.setLastDate(new Date());
            return repo.save(newKey);
        } else {
            newKey.setSearchedHits(newKey.getSearchedHits()+1);
            newKey.setLastDate(new Date());
            return repo.save(newKey);
        }
    }
}
