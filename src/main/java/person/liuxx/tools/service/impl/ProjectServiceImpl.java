package person.liuxx.tools.service.impl;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URLEncoder;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;

import person.liuxx.tools.service.ProjectService;

/**
 * @author 刘湘湘
 * @version 1.0.0<br>
 *          创建时间：2018年3月30日 下午4:28:42
 * @since 1.0.0
 */
public class ProjectServiceImpl implements ProjectService
{
    private Logger log = LoggerFactory.getLogger(ProjectServiceImpl.class);

    @Override
    public ResponseEntity<Resource> springbootProject()
    {
        ResponseEntity<Resource> result = null;
        Path file = Paths.get("");
        Resource resource = null;
        try
        {
            resource = new UrlResource(file.toUri());
            result = ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename*=UTF-8''" + URLEncoder.encode(
                                    "合并后的表格.xlsx", "UTF-8") + "")
                    .body(resource);
        } catch (MalformedURLException | UnsupportedEncodingException e)
        {
            log.error("获取资源时发生异常：" + e);
        }
        return result;
    }
}
