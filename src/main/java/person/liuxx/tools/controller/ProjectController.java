package person.liuxx.tools.controller;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import person.liuxx.tools.dto.ReactProjectDTO;
import person.liuxx.tools.exception.CreateZipException;
import person.liuxx.tools.service.ProjectService;
import person.liuxx.util.service.reponse.EmptySuccedResponse;

/**
 * @author 刘湘湘
 * @version 1.0.0<br>
 *          创建时间：2018年3月30日 下午4:10:21
 * @since 1.0.0
 */
@Controller
public class ProjectController
{
    @Autowired
    private ProjectService projectService;

    @GetMapping(value = "/project/springboot/zipfile")
    @ResponseBody
    public ResponseEntity<Resource> springbootProject()
    {
        return projectService.springbootProject();
    }

    @GetMapping(value = "/project/react/zipfile")
    @ResponseBody
    public ResponseEntity<Resource> reactProjectZipFileTest(HttpSession session)
    {
        return projectService.getReactProject(session).<CreateZipException> orElseThrow(() ->
        {
            throw new CreateZipException("获取session中的ZIP文件资源失败，session id :" + session.getId());
        });
    }

    @PostMapping(value = "/project/react/sessionfile")
    @ResponseBody
    public EmptySuccedResponse reactProjectZipFile(@RequestBody ReactProjectDTO project,
            HttpSession session)
    {
        return projectService.createSessionReactProject(project, session).<CreateZipException> orElseThrow(() ->
        {
            throw new CreateZipException("生成zip文件失败，生成参数：" + project);
        });
    }
}
