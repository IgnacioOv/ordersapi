package ordersapi;


import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.PrimaryKey;

public class ReadOrderLambda {
    
    ObjectMapper objectMapper = new ObjectMapper();
    AmazonDynamoDB dynamoDB = AmazonDynamoDBClientBuilder.defaultClient();
    DynamoDB db = new DynamoDB(dynamoDB);
    
    public APIGatewayProxyResponseEvent readOrder(APIGatewayProxyRequestEvent request) throws JsonProcessingException{
    
        Table table = db.getTable(System.getenv("ORDERS_TABLE"));

        // Obtener el ID del pedido desde la solicitud
        String orderId = request.getPathParameters().get("id");

        // Crear una clave primaria para buscar el pedido
        PrimaryKey primaryKey = new PrimaryKey("id", orderId);

        // Obtener el pedido de DynamoDB
        Item item = table.getItem(primaryKey);

        // Verificar si el pedido existe
        if (item != null) {
            // Convertir el item de DynamoDB a un objeto Order
            Order order = new Order(item.getString("id"), item.getString("itemName"), item.getInt("quantity"));
            
            // Convertir el objeto Order a JSON
            String jsonOutput = objectMapper.writeValueAsString(order);
            
            // Devolver la respuesta con el pedido encontrado
            return new APIGatewayProxyResponseEvent()
                    .withStatusCode(200)
                    .withBody(jsonOutput);
        } else {
            // Devolver una respuesta indicando que el pedido no se encontró
            return new APIGatewayProxyResponseEvent()
                    .withStatusCode(404)
                    .withBody("Order not found");
        }
    }
}