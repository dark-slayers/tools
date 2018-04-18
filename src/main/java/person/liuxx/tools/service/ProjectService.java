package person.liuxx.tools.service;

import java.util.Optional;

import javax.servlet.http.HttpSession;

import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;

import person.liuxx.tools.dto.ReactProjectDTO;
import person.liuxx.tools.dto.SpringBootProjectDTO;
import person.liuxx.util.service.reponse.EmptySuccedResponse;

/**
 * @author 刘湘湘
 * @version 1.0.0<br>
 *          创建时间：2018年3月30日 下午4:26:53
 * @since 1.0.0
 */
public interface ProjectService
{
    Optional<EmptySuccedResponse> updateSpringBootProject(SpringBootProjectDTO project);

    /**
     * @author 刘湘湘
     * @version 1.0.0<br>
     *          创建时间：2018年4月17日 下午2:14:12
     * @since 1.0.0
     * @param project
     * @return
     */
    Optional<EmptySuccedResponse> createSessionReactProject(ReactProjectDTO project,
            HttpSession session);

    /**
     * @author 刘湘湘
     * @version 1.0.0<br>
     *          创建时间：2018年4月17日 下午4:04:55
     * @since 1.0.0
     * @param session
     * @return
     */
    Optional<ResponseEntity<Resource>> getReactProject(HttpSession session);
}
