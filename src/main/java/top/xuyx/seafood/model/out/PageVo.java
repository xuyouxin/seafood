package top.xuyx.seafood.model.out;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class PageVo {
    private int total;
    private int size;
    private int pages;
    private int current;

    private Object content;
}
