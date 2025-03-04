package com.fool.gamearchivemanager.entity.po;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Data
public class GameArchive {

    @TableId(type = IdType.AUTO)
    private Integer id;

    private String gameName;

    private String md5;

    private Integer uid;

    private String archivePath;

    private Integer archiveFileStatus;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date saveTime;

    private Date uploadTime;


    public GameArchive(String gameName, String md5, Integer uid, String archivePath, Date saveTime) {
        this.gameName = gameName;
        this.md5 = md5;
        this.uid = uid;
        this.archivePath = archivePath;
        this.saveTime = saveTime;
    }

    public GameArchive(String gameName, String md5, Integer uid, String archivePath, String saveTime, String pattern) throws ParseException {
        this.gameName = gameName;
        this.md5 = md5;
        this.uid = uid;
        this.archivePath = archivePath;

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        this.saveTime = simpleDateFormat.parse(saveTime);
    }

    public GameArchive() {
    }
}
