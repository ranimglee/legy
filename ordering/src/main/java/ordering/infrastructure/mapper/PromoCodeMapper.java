package ordering.infrastructure.mapper;


import ordering.domain.model.PromoCode;
import ordering.infrastructure.Document.PromoCodeDocument;

public class PromoCodeMapper {

    public static PromoCode toDomain(PromoCodeDocument doc) {
        return new PromoCode(
                doc.getId(),
                doc.getCode(),
                doc.getDiscountValue(),
                doc.getStartDate(),
                doc.getEndDate(),
                doc.getMaxUsage(),
                doc.getCurrentUsage()
        );
    }

    public static PromoCodeDocument toDocument(PromoCode promo) {
        PromoCodeDocument doc = new PromoCodeDocument();
        doc.setId(promo.getId());
        doc.setCode(promo.getCode());
        doc.setDiscountValue(promo.getDiscountValue());
        doc.setStartDate(promo.getStartDate());
        doc.setEndDate(promo.getEndDate());
        doc.setMaxUsage(promo.getMaxUsage());
        doc.setCurrentUsage(promo.getCurrentUsage());
        return doc;
    }
}
