package person.liuxx.tools.config;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

import person.liuxx.util.log.LogUtil;

/**
 * @author 刘湘湘
 * @version 1.0.0<br>
 *          创建时间：2017年8月30日 上午10:57:37
 * @since 1.0.0
 */
@Configuration
@ComponentScan
public class ElConfig
{
    private Logger log = LoggerFactory.getLogger(ElConfig.class);
    @Value("classpath:libs/project/javascript/react")
    private Resource reactProjectPath;

    public Optional<Path> reactProjectPath()
    {
        return Optional.ofNullable(reactProjectPath).map(r -> getFile(r)).map(f -> f.toPath());
    }

    private File getFile(Resource resource)
    {
        try
        {
            return resource.getFile();
        } catch (IOException e)
        {
            log.error(LogUtil.errorInfo(e));
            return null;
        }
    }
}
