package person.liuxx.tools.service;

import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;

/**
 * @author 刘湘湘
 * @version 1.0.0<br>
 *          创建时间：2018年3月30日 下午4:26:53
 * @since 1.0.0
 */
public interface ProjectService
{
    ResponseEntity<Resource> springbootProject();
}
