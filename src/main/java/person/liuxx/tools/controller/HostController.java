package person.liuxx.tools.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import person.liuxx.tools.service.HostsService;
import person.liuxx.util.service.exception.SearchException;

/**
 * @author 刘湘湘
 * @version 1.0.0<br>
 *          创建时间：2018年4月11日 下午3:48:17
 * @since 1.0.0
 */
@Controller
@Api(value = "hosts控制器")
public class HostController
{
    @Autowired
    private HostsService service;

    @ApiOperation(value = "下载hosts文件", notes = "下载hosts文件")
    @GetMapping("/hosts/file")
    @ResponseBody
    public ResponseEntity<Resource> hostFile()
    {
        return service.getHostsFile().<SearchException> orElseThrow(() ->
        {
            throw new SearchException("获取hosts文件失败！");
        });
    }
}
