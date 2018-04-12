package person.liuxx.tools.service;

import java.util.Optional;

import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;

/** 
* @author  刘湘湘 
* @version 1.0.0<br>创建时间：2018年4月11日 下午3:52:20
* @since 1.0.0 
*/
public interface HostsService
{
    Optional<ResponseEntity<Resource>> getHostsFile();
}
