package restaurant.infrastructure.service;

import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import restaurant.application.dto.SearchCriteria;
import restaurant.application.dto.SearchResultDTO;
import restaurant.infrastructure.Document.MongoProduct;
import restaurant.infrastructure.Document.MongoRestaurant;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class SearchService {

    private final MongoTemplate mongoTemplate;

    public List<SearchResultDTO> search(SearchCriteria c) {
        List<SearchResultDTO> results = new ArrayList<>();

        // 1) build our case‐insensitive regex (only if query ≥3 chars)
        Pattern contains = null;
        if (c.getQuery() != null && c.getQuery().length() >= 3) {
            String esc = Pattern.quote(c.getQuery());
            contains = Pattern.compile(".*" + esc + ".*", Pattern.CASE_INSENSITIVE);
        }

        // === 2) RESTAURANTS ===
        Criteria restCrit = new Criteria();
        if (contains != null) {
            restCrit = new Criteria().orOperator(
                    Criteria.where("nom").regex(contains),
                    Criteria.where("adresse").regex(contains)
            );
        }
        if (c.getCuisineType() != null) {
            restCrit = restCrit.and("mainCuisineType").is(c.getCuisineType());
        }
        if (c.getMinRating() != null) {
            restCrit = restCrit.and("averageRating").gte(c.getMinRating());
        }

        List<MongoRestaurant> rests =
                mongoTemplate.find(new Query(restCrit), MongoRestaurant.class);
        results.addAll(mapToSearchResults(rests, "restaurant"));

        // === 3) PRODUCTS ===
        Criteria prodCrit = new Criteria();
        if (contains != null) {
            prodCrit = new Criteria().orOperator(
                    Criteria.where("name").regex(contains),
                    Criteria.where("description").regex(contains)
            );
        }
        if (c.getCategoryId() != null) {
            // match the DBRef id
            prodCrit = prodCrit.and("category.$id")
                    .is(new ObjectId(c.getCategoryId()));
        }
        // price range
        if (c.getMinPrice() != null && c.getMaxPrice() != null) {
            prodCrit = prodCrit.andOperator(
                    Criteria.where("price").gte(c.getMinPrice()),
                    Criteria.where("price").lte(c.getMaxPrice())
            );
        } else if (c.getMinPrice() != null) {
            prodCrit = prodCrit.and("price").gte(c.getMinPrice());
        } else if (c.getMaxPrice() != null) {
            prodCrit = prodCrit.and("price").lte(c.getMaxPrice());
        }

        List<MongoProduct> prods =
                mongoTemplate.find(new Query(prodCrit), MongoProduct.class);
        results.addAll(mapToSearchResults(prods, "product"));

        return results;
    }

    private List<SearchResultDTO> mapToSearchResults(List<?> entities, String type) {
        List<SearchResultDTO> out = new ArrayList<>();
        for (Object e : entities) {
            if (e instanceof MongoRestaurant r) {
                out.add(new SearchResultDTO(r.getNom(), r.getAdresse(), type, r.getId()));
            } else if (e instanceof MongoProduct p) {
                out.add(new SearchResultDTO(p.getName(), p.getDescription(), type, p.getId()));
            }
        }
        return out;
    }
}
