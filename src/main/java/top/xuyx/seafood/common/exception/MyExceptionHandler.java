package top.xuyx.seafood.common.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import top.xuyx.seafood.common.Response;

@RestControllerAdvice
@Slf4j
public class MyExceptionHandler {

	@ExceptionHandler(value = Exception.class)
	public Response defaultExceptionHandler(Exception exception) throws Exception {
		Response result = new Response();
		try {
			throw exception;
		} catch (ExpectedException e) {
			log.error(e.getMessage());
			result.setCode(e.getCode());
			result.setMessage(e.getMsg());
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			result.setCode(500);
			result.setMessage("内部服务器错误");
		}
		log.info(result.toString());
		return result;
	}
}
