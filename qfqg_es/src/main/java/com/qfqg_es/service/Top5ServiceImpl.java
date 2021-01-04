package com.qfqg_es.service;

import com.qfqg_es.model.Keyword;
import com.qfqg_es.repository.Top5Repository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class Top5ServiceImpl implements Top5Service {
    @Autowired
    private Top5Repository top5Repository;

    private static final Logger logger = LoggerFactory.getLogger(Top5ServiceImpl.class);

    @Override
    public List<Keyword> top5Search() {
        List<Keyword> topKeys = new ArrayList<>();
        try{
            topKeys = top5Repository.findAllOrderByHitsAndDate().get();
            if(topKeys.size()>9){
                return topKeys.subList(0,10);
            } else {
                return topKeys;
            }
        } catch (Exception e){
            logger.warn("热搜列表为空");
            return null;
        }
    }

    @Override
    public Keyword updateTopK(String keyword) {

        if(!top5Repository.existsByKeywordName(keyword)){
            Keyword newKey = new Keyword();
            newKey.setKeywordName(keyword);
            newKey.setSearchedHits(1);
            newKey.setLastDate(new Date());
            return top5Repository.save(newKey);
        } else {
            Keyword newKey = top5Repository.findByKeywordName(keyword).get();
            newKey.setSearchedHits(newKey.getSearchedHits()+1);
            newKey.setLastDate(new Date());
            return top5Repository.save(newKey);
        }
    }
}
