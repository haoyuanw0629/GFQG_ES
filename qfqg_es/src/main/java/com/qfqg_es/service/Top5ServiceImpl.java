package com.qfqg_es.service;

import com.qfqg_es.repository.Top5Repository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class Top5ServiceImpl implements Top5Service {
    @Autowired
    private Top5Repository repo;

    @Override
    public List top5Search() {

        return null;
    }
}
