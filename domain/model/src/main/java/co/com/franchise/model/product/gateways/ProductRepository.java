package co.com.franchise.model.product.gateways;

import co.com.franchise.model.product.Product;
import co.com.franchise.model.product.ProductParam;
import reactor.core.publisher.Mono;

public interface ProductRepository {
    Mono<Product> save(ProductParam productParam);
}
