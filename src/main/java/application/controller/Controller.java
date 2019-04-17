package application.controller;

import application.proxy.AmazonProxy;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@RestController
public class Controller {

    @Autowired
    AmazonProxy amazonProxy ;

    @RequestMapping("/estimate")
    public String estimate(@RequestParam String keyword){

        int rank = getRank(keyword);
        JSONObject json = new JSONObject();
        json.put("keyword", keyword);
        json.put("score", rank);
        return json.toString();
    }

    /**
     *
     * @param keyword
     * @return
     *
     * This function calculates the number of products that are less in ranking than the
     * keyword passed.
     *
     * This happens by searching on 2 levels, first search by using the keyword itself, then the second
     * level of search is for every result we got from the first search.
     * The second search makes us (relatively) know the number of products or keywords that didn't appear
     * in the first search which means they have the same prefix but have less ranking.
     * This is not very accurate as the search result is only 10, so there might be larger numbers
     * of keywords that comes after the keyword.
     *
     * In case the keyword is too generic (example : bla -> gets all products starting with black)
     * then the score here will be the the size of the tree (which means the total number of results
     * we will get from the second search).
     *
     * In case of empty list we consider the score 0.
     *
     */
    private int getRank(String keyword){
        int countOfProductsLessInRanking = 0 ;
        List<String> result = amazonProxy.getListFromAmazon(keyword);

        if(!result.isEmpty()) {
            int index = result.indexOf(keyword);
            if(index == -1){
                countOfProductsLessInRanking = result.size();
            }else {
                countOfProductsLessInRanking = result.size() - index;
            }
            Set<String> seenProducts = new HashSet<>(result);
            for (String product : result) {
                List<String> resultForOneProduct = amazonProxy.getListFromAmazon(product);
                for (String newProduct : resultForOneProduct) {
                    if (!seenProducts.contains(newProduct)) {
                        countOfProductsLessInRanking++;
                        seenProducts.add(newProduct);
                    }
                }
            }
        }
        return countOfProductsLessInRanking;
    }
}
