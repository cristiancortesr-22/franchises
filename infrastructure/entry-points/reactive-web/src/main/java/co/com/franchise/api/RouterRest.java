package co.com.franchise.api;

import co.com.franchise.api.branch.BranchHandler;
import co.com.franchise.api.branch.dto.BranchRequest;
import co.com.franchise.api.dto.error.APIErrorResponse;
import co.com.franchise.api.dto.success.APISuccessResponse;
import co.com.franchise.api.franchise.FranchiseHandler;
import co.com.franchise.api.franchise.dto.FranchiseRequest;
import co.com.franchise.api.product.ProductHandler;
import co.com.franchise.api.product.dto.ProductRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springdoc.core.annotations.RouterOperation;
import org.springdoc.core.annotations.RouterOperations;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class RouterRest {

    @Bean
    @RouterOperations({
            @RouterOperation(path = "/franchise", method = RequestMethod.POST,
                    beanClass = FranchiseHandler.class, beanMethod = "create",
                    operation = @Operation(operationId = "createFranchise", summary = "Create a new franchise", tags = {"Franchise"},
                            requestBody = @RequestBody(content = @Content(schema = @Schema(implementation = FranchiseRequest.class))),
                            responses = {
                                    @ApiResponse(responseCode = "200", description = "Franchise created", content = @Content(schema = @Schema(implementation = APISuccessResponse.class))),
                                    @ApiResponse(responseCode = "400", description = "Invalid input", content = @Content(schema = @Schema(implementation = APIErrorResponse.class)))
                            })),
            @RouterOperation(path = "/branch", method = RequestMethod.POST,
                    beanClass = BranchHandler.class, beanMethod = "create",
                    operation = @Operation(operationId = "createBranch", summary = "Add a branch to a franchise", tags = {"Branch"},
                            requestBody = @RequestBody(content = @Content(schema = @Schema(implementation = BranchRequest.class))),
                            responses = {
                                    @ApiResponse(responseCode = "200", description = "Branch created", content = @Content(schema = @Schema(implementation = APISuccessResponse.class))),
                                    @ApiResponse(responseCode = "400", description = "Invalid input", content = @Content(schema = @Schema(implementation = APIErrorResponse.class)))
                            })),
            @RouterOperation(path = "/product", method = RequestMethod.POST,
                    beanClass = ProductHandler.class, beanMethod = "create",
                    operation = @Operation(operationId = "createProduct", summary = "Add a product to a branch", tags = {"Product"},
                            requestBody = @RequestBody(content = @Content(schema = @Schema(implementation = ProductRequest.class))),
                            responses = {
                                    @ApiResponse(responseCode = "200", description = "Product created", content = @Content(schema = @Schema(implementation = APISuccessResponse.class))),
                                    @ApiResponse(responseCode = "400", description = "Invalid input", content = @Content(schema = @Schema(implementation = APIErrorResponse.class)))
                            }))
    })
    public RouterFunction<ServerResponse> routerFunction(FranchiseHandler franchiseHandler, BranchHandler branchHandler,
                                                         ProductHandler productHandler) {
        return route(POST("/franchise"), franchiseHandler::create)
                .andRoute(POST("/branch"), branchHandler::create)
                .andRoute(POST("/product"), productHandler::create);
    }
}
