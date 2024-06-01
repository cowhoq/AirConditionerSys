package org.app.common;

import lombok.Data;
import lombok.Getter;

import java.util.List;

@Data
@Getter
public class WorkStatus {
    private WorkMode workmode;

    private List<Integer> range;
}
