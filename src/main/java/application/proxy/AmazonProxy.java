package application.proxy;

import org.apache.http.client.utils.URIBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class AmazonProxy {

    @Autowired
    RestTemplate restTemplate;

    @Value("${market}")
    String market;

    @Value("${search_alias}")
    String search_alias;

    @Value("${url}")
    String url;

    public List<String> getListFromAmazon(String keyword){
        String result = callAmazonAutoCompleteService(keyword);
        return parseResultAsList(result);
    }

    private String callAmazonAutoCompleteService(String keyword){
        try {
            URIBuilder builder = new URIBuilder(url);
            builder.addParameter("mkt", market);
            builder.addParameter("search-alias", search_alias);
            builder.addParameter("q", keyword);
            URI uri = builder.build().toURL().toURI();
            String s = restTemplate.getForObject(uri, String.class);
            System.out.println(s);
            return s ;
        } catch (URISyntaxException | MalformedURLException e) {
            e.printStackTrace();
        }
        return "";
    }

    private List<String> parseResultAsList(String result){
        result = result.substring(1);
        int start = result.indexOf("[");
        int end = result.indexOf("]");
        String listString = result.substring(start+1,end);
        if(listString.contains(",")) {
            String[] array = listString.split(",");
            return Arrays.stream(array).map(word -> word.replaceAll("\"", "")).collect(Collectors.toList());
        }
        return new ArrayList<>();
    }
}
