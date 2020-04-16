package run.halo.app.repository;

import lombok.Data;

@Data
public class CsdnBean {
    //    {
//        "article_id": "36398755",
//            "title": "HTTPClient",
//            "description": "",
//            "content": "",
//            "markdowncontent": "",
//            "tags": "",
//            "categories": "java",
//            "type": "original",
//            "status": 1,
//            "read_type": "public",
//            "reason": "",
//            "resource_url": "",
//            "original_link": "",
//            "authorized_status": false,
//            "check_original": false,
//            "editor_type": 0
//    }
    private String article_id;
    private String title;
    private String description;
    private String content;
    private String markdowncontent;
    private String tags;
    private String categories;
    private String reason;
    private String resource_url;
    private String original_link;


}
