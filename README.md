# MCP with OAuth

This sample demonstrates how to secure an MCP server using OAuth2, as per
the [MCP specification](https://spec.modelcontextprotocol.io/specification/2025-03-26/basic/authorization/).

## Getting started

Run the project with:

```
./mvnw spring-boot:run
```

## Authentication Server Discovery

The authentication server allows clients to automatically obtain information about the server's endpoints and capabilities.

You can fetch the discovery document at:

```
http://localhost:8080/.well-known/oauth-authorization-server
```

Example:

```shell
curl http://localhost:8080/.well-known/oauth-authorization-server | jq
```

This document provides important metadata, including:
- Authorization endpoint
- Token endpoint
- Registration endpoint
- Supported scopes
- Supported grant types

Clients should use this document to configure themselves dynamically and to discover the correct endpoints for authentication and token operations.

## Fetching an Initial Token for Dynamic Client Registration

To register a new client at the `/connect/register` endpoint, you need an access token with the `client.create` scope. You can obtain this using the `client_credentials` grant with a pre-registered client that has the appropriate permissions:

```shell
curl -XPOST "http://localhost:8080/oauth2/token" \
  --data grant_type=client_credentials \
  --data scope=client.create \
  --user "registrar-client:secret"
```

The response will include an access token. Use this token as a Bearer token in the `Authorization` header when calling `/connect/register`.

## Dynamic Client Registration

You can register a new OAuth2 client dynamically by calling the `/connect/register` endpoint. For example:

```shell
curl -XPOST "http://localhost:8080/connect/register" \
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "client_name": "my-dynamic-client",
    "redirect_uris": ["http://localhost:3000/callback"],
    "grant_types": ["authorization_code", "refresh_token"],
    "token_endpoint_auth_method": "client_secret_basic"
  }'
```

Replace `YOUR_ACCESS_TOKEN` with the token you received.

The response will include the `client_id` and `client_secret` for your new client.

## Browser-based Authentication (Authorization Code Flow)

To authenticate using the browser (Authorization Code Grant):

1. **Register your client** (see above) and note the `client_id`, `client_secret`, and `redirect_uri`.
2. **Open the authorization URL in your browser:**

   ```
   http://localhost:8080/oauth2/authorize?response_type=code&client_id=YOUR_CLIENT_ID&redirect_uri=YOUR_REDIRECT_URI&scope=weather.read
   ```

   Replace `YOUR_CLIENT_ID` and `YOUR_REDIRECT_URI` with your values.

3. **Login and approve** the request. You will be redirected to your `redirect_uri` with a `code` parameter.
4. **Exchange the code for a token:**

   ```shell
   curl -XPOST "http://localhost:8080/oauth2/token" \
     --data grant_type=authorization_code \
     --data code=AUTH_CODE \
     --data redirect_uri=YOUR_REDIRECT_URI \
     --user "YOUR_CLIENT_ID:YOUR_CLIENT_SECRET"
   ```

   Replace `AUTH_CODE`, `YOUR_CLIENT_ID`, `YOUR_CLIENT_SECRET`, and `YOUR_REDIRECT_URI` with your values.

5. The response will include your access token.

Store that token, and then boot up the MCP inspector:

```shell
npx @modelcontextprotocol/inspector@0.6.0
```

In the MCP inspector, paste your token. Click connect. Note that the token is only valid for 15 minutes.

## Implementation considerations

### Dependencies

In Spring, OAuth2 Support for MCP server means adding:

1. [Spring Security](https://docs.spring.io/spring-security/) (infrastructure for security)
2. [Spring Authorization Server](https://docs.spring.io/spring-authorization-server/) (issuing tokens)
3. [Spring Security: OAuth2 Resource Server](https://docs.spring.io/spring-security/reference/servlet/oauth2/resource-server/index.html#page-title) (
   authentication using tokens)

Note that Spring Auth Server does not support the reactive stack, so issuing tokens only works in Servlet.
