package person.liuxx.tools.service;

import java.util.Optional;

import person.liuxx.tools.dto.JavaCodeApiDTO;
import person.liuxx.util.service.reponse.EmptySuccedResponse;

/**
 * @author 刘湘湘
 * @since 2019年4月10日 下午3:28:44
 */
public interface JavaCodeService
{
    Optional<EmptySuccedResponse> createAPI(JavaCodeApiDTO javaCodeApiDTO);
}
