package com.qfqg_es.service;


import com.qfqg_es.model.Keyword;

import java.util.List;

public interface Top5Service {
    //获取排名前5的关键字
    List<Keyword> top5Search();
    //创建或更新关键字
    Keyword updateTopK(String keyword);

}
