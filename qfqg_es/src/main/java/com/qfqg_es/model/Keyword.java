package com.qfqg_es.model;

import lombok.Data;
import javax.persistence.*;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import java.util.Date;

@Data
@Entity
public class Keyword {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long KeywordId;
    private String keywordName;
    private long searchedHits;
    private Date lastDate;

    public Keyword(){ }

}
