package com.qfqg_es.model;

import lombok.Data;
import org.springframework.data.annotation.Id;

import javax.persistence.Entity;

@Data
@Entity
public class TopK {
    @Id
    private  long id;
    private  String keywordName;
    private int searchedHits;

    public TopK(String keywordName, int searchedHits) {
        this.keywordName = keywordName;
        this.searchedHits = searchedHits;
    }
}
