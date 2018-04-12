package person.liuxx.tools.service.impl;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import person.liuxx.tools.service.HostsService;
import person.liuxx.util.log.LogUtil;

/**
 * @author 刘湘湘
 * @version 1.0.0<br>
 *          创建时间：2018年4月11日 下午3:58:58
 * @since 1.0.0
 */
@Service
public class HostsServiceImpl implements HostsService
{
    private Logger log = LoggerFactory.getLogger(HostsServiceImpl.class);
    private static final String URL = "https://github.com/googlehosts/hosts/blob/master/hosts-files/hosts";

    @Override
    public Optional<ResponseEntity<Resource>> getHostsFile()
    {
        try (CloseableHttpClient httpclient = HttpClients.createDefault();)
        {
            HttpGet httpget = new HttpGet(URL);
            log.info("Executing request {}", httpget.getRequestLine());
            // Create a custom response handler
            ResponseHandler<String> responseHandler = new ResponseHandler<String>()
            {
                @Override
                public String handleResponse(final HttpResponse response)
                        throws ClientProtocolException, IOException
                {
                    int status = response.getStatusLine().getStatusCode();
                    if (status >= 200 && status < 300)
                    {
                        HttpEntity entity = response.getEntity();
                        return entity != null ? EntityUtils.toString(entity) : null;
                    } else
                    {
                        throw new ClientProtocolException("Unexpected response status: " + status);
                    }
                }
            };
            String responseBody = httpclient.execute(httpget, responseHandler);
            Document doc = Jsoup.parse(responseBody);
            Elements tables = doc.getElementsByClass("highlight tab-size js-file-line-container");
            Elements tds = Optional.ofNullable(tables)
                    .filter(t -> t.size() > 0)
                    .map(t -> t.get(0))
                    .map(t -> t.getElementsByClass("blob-code blob-code-inner js-file-line"))
                    .orElse(new Elements());
            String text = tds.stream().map(td -> td.text()).collect(Collectors.joining("\n"));
            InputStream is = new BufferedInputStream(new ByteArrayInputStream(text.getBytes(
                    "UTF-8")));
            Resource resource = new InputStreamResource(is);
            ResponseEntity<Resource> result = ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename*=UTF-8''"
                            + URLEncoder.encode("hosts", "UTF-8") + "")
                    .body(resource);
            return Optional.of(result);
        } catch (IOException e)
        {
            log.error(LogUtil.errorInfo(e));
        }
        return Optional.empty();
    }
}
