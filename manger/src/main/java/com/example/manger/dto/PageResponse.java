package com.example.manger.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

/**
 * 分页响应DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PageResponse<T> {
    private List<T> content;         // 数据内容
    private Integer page;            // 当前页码
    private Integer size;            // 每页大小
    private Long total;              // 总记录数
    private Integer totalPages;      // 总页数
    private Boolean first;           // 是否第一页
    private Boolean last;            // 是否最后一页
    private Boolean hasNext;         // 是否有下一页
    private Boolean hasPrevious;     // 是否有上一页
    
    // 便捷构造方法
    public static <T> PageResponse<T> of(List<T> content, Integer page, Integer size, Long total) {
        int totalPages = (int) Math.ceil((double) total / size);
        return PageResponse.<T>builder()
                .content(content)
                .page(page)
                .size(size)
                .total(total)
                .totalPages(totalPages)
                .first(page == 1)
                .last(page.equals(totalPages))
                .hasNext(!page.equals(totalPages))
                .hasPrevious(page != 1)
                .build();
    }
}