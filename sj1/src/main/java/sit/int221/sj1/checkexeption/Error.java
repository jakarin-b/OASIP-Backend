package sit.int221.sj1.checkexeption;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

import java.util.Map;

@Data
@RequiredArgsConstructor
public class Error {
    private final String timeStamp;
    private final int statusCode;
    private final String path;
    private final HttpStatus httpStatus;
    private final String error;
    private final Map<String,String> fieldError;
}
