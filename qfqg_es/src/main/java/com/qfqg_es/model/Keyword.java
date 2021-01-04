package com.qfqg_es.model;

import lombok.Data;
import org.springframework.data.annotation.Id;

import javax.persistence.Entity;
import java.util.Date;

@Data
//@Entity
public class Keyword {
    @Id
    private long id;
    private String keywordName;
    private long searchedHits;
    private Date lastDate;

    public Keyword(){ }

}
