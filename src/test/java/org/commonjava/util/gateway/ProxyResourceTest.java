package org.commonjava.util.gateway;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import org.commonjava.util.gateway.fixture.TestResources;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.Date;

import static io.restassured.RestAssured.given;
import static org.commonjava.util.gateway.fixture.TestResources.*;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.Matchers.anyOf;

@QuarkusTestResource( TestResources.class )
@QuarkusTest
public class ProxyResourceTest
{
    @Test
    public void testProxyGet()
    {
        given().when()
               .get( METADATA_PATH )
               .then()
               .statusCode( 200 )
               .body( is( METADATA_CONTENT ) );
    }

    @Test
    public void testProxyGet404()
    {
        given().when()
               .get( NON_EXIST_PATH )
               .then()
               .statusCode( 404 );
    }

    @Test
    public void testProxyGetServiceNotFound()
    {
        given().when()
               .get( SERVICE_NOT_FOUND_PATH )
               .then()
               .statusCode( 400 )
               .body( containsString( "Service not found" ) );
    }

    @Test
    public void testProxyGetBytes()
    {
        given().when()
               .get( BYTE_FILE_PATH )
               .then()
               .statusCode( 200 )
               .body( notNullValue() );
    }

    @Test
    public void testProxyHead()
    {
        given().when()
               .head( METADATA_PATH )
               .then()
               .statusCode( 200 )
               .header( ORIGIN, is( ORIGIN_VALUE ) );
    }

    @Test
    public void testProxyHead404()
    {
        given().when()
               .head( NON_EXIST_PATH )
               .then()
               .statusCode( 404 );
    }

    @Test
    public void testProxyPost()
    {
        /* @formatter:off */
        String body = "{"
                        + "  \"key\": \"maven:hosted:local-deployments\","
                        + "  \"type\": \"hosted\","
                        + "  \"packageType\": \"maven\","
                        + "  \"name\": \"local-deployments\""
                        + "}";
        /* @formatter:on */
        given().when()
               .body( body )
               .post( POST_PATH )
               .then()
               .statusCode( anyOf( is( 200 ), is( 201 ) ) )
               .body( is( body ) ); // mock server return same body
    }

    @Test
    public void testProxyPut()
    {
        given().when()
               .body( "This is a test " + new Date() )
               .put( PUT_PATH )
               .then()
               .statusCode( anyOf( is( 201 ), is( 204 ) ) );
    }

    @Test
    public void testPromote()
    {
        given().when()
               .post( PROMOTE_PATH )
               .then()
               .statusCode( is( 200 ) );
    }

    @Test
    public void testProxyTimeout()
    {
        given().when()
               .post( PROMOTE_TIMEOUT_PATH )
               .then()
               .statusCode( is( 500 ) )
               .body( containsString( "timeout" ) );
    }

    @Test
    @Disabled("java.lang.NoSuchMethodError: 'java.nio.channels.ByteChannel org.eclipse.jetty.io.ChannelEndPoint.getChannel()'")
    public void testProxyRetry()
    {
        given().when().post( EXCEPTION_PATH )
               .then()
               .statusCode( is( 500 ) )
               .body( containsString( "Retries exhausted" ) );
    }

}