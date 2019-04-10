package person.liuxx.tools.controller;

import javax.servlet.http.HttpSession;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;
import person.liuxx.tools.dto.JavaCodeApiDTO;
import person.liuxx.tools.exception.CreateCodeException;
import person.liuxx.tools.exception.CreateZipException;
import person.liuxx.tools.service.JavaCodeService;
import person.liuxx.util.service.reponse.EmptySuccedResponse;

/**
 * @author 刘湘湘
 * @since 2019年4月10日 下午2:14:15
 */
@RestController
@Api(value = "Java项目Code生成器")
public class JavaCodeController
{
    private JavaCodeService javaCodeService;
    @PostMapping(value = "/code/java/api")
    @ResponseBody
    public EmptySuccedResponse reactProjectSessionFile(@RequestBody JavaCodeApiDTO javaCodeApiDTO,
            HttpSession session)
    {
        return javaCodeService.createAPI(javaCodeApiDTO)
                .<CreateZipException> orElseThrow(() ->
                {
                    throw new CreateCodeException("生成zip文件失败，生成参数：");
                });
    }
}
