package org.app.entity.dto;

import lombok.Data;
import lombok.Getter;
import org.app.entity.WorkMode;

import java.util.List;

@Data
@Getter
public class WorkStatus {
    private WorkMode workmode;

    private List<Integer> range;
}
