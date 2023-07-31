package com.example.movieex.util;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class PagingUtil {
    private int totalPage;
    private int pageNum;
    private int pageCnt;
    private String urlStr;

    public String makePaging(){
        String pageHtml = null;
        StringBuffer sb = new StringBuffer();

        int curGroup = (pageNum % pageCnt) > 0 ?
                pageNum/pageCnt + 1 : pageCnt/pageCnt;

        int start = (curGroup * pageCnt) - (pageCnt - 1);

        int end = (curGroup * pageCnt) >= totalPage ?
                totalPage : curGroup * pageCnt;

        if(start != 1){
            sb.append("<a class='pno' href='/" + urlStr + "pageNum="
                    + (start - 1) + "'>" + "◀</a>" );
        }

        for(int i = start; i <= end; i++){
            if(pageNum == i){
                sb.append("<font class='pno'>" + i + "</font>");
            } else {
                sb.append("<a class='pno' href='/" + urlStr + "pageNum="
                + i + "'>" + i + "</a>");
            }
        }

        if(end != totalPage){
            sb.append("<a class='pno' href='/" + urlStr + "pageNum="
                    + (end + 1) + "'>" + "▶</a>");
        }
        pageHtml = sb.toString();
        return pageHtml;
    }
}
