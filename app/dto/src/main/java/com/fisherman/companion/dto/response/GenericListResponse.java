package com.fisherman.companion.dto.response;

import java.util.Collection;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GenericListResponse<T> {
    private Long count;
    private List<T> dataList;

    public static <T> GenericListResponse<T> of(Collection<T> items) {
        return new GenericListResponse<>((long) items.size(), List.copyOf(items));
    }
}
