package searchengineDR.dto.result;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import searchengineDR.dto.statistics.SearchStatistics;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Result {
    private boolean result;
    private String error;
    private Integer count;
    private List<SearchStatistics> data;

}
