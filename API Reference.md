* /api
    * /manage
        * /clients
            * /
                * GET
                    * user - Username of the seller.
                    * pass - Password of the seller.
                * PUT
                    * user - Username of the seller.
                    * pass - Password of the seller.
                    * name - Name of the client.
                    * is_company - Is the client a company.
                * POST
                    * user - Username of the seller.
                    * pass - Password of the seller.
                    * change - Name of the client.
                    * name - Name to be assigned to the client.
                * DELETE
                    * user - Username of the seller.
                    * pass - Password of the seller.
                    * name - Name of the client.
            * /{page}
                * GET
                    * user - Username of the seller.
                    * pass - Password of the seller.
        * /products
            * /
                * GET
                    * user - Username of the administrator.
                    * pass - Password of the administrator.
                * PUT
                    * user - Username of the administrator.
                    * pass - Password of the administrator.
                    * name - Name of the product.
                    * price - Price of the product.
                * POST
                    * user - Username of the administrator.
                    * pass - Password of the administrator.
                    * change - Name of the product.
                    * name ? - Name to be assigned to the product.
                    * price ? - Price to be assigned to the product.
                * DELETE
                    * user - Username of the administrator.
                    * pass - Password of the administrator.
                    * name - Name of the client.
            * /{page}
                * GET
                    * user - Username of the administrator.
                    * pass - Password of the administrator.
        * /users
            * /
                * GET
                    * user - Username of the administrator.
                    * pass - Password of the administrator.
                * PUT
                    * user - Username of the administrator.
                    * pass - Password of the administrator.
                    * name - Name of the user.
                    * is_admin - Is the user administrator.
                * POST
                    * user - Username of the administrator.
                    * pass - Password of the administrator.
                    * change - Name of the user.
                    * name ? - Name to be assigned to the user.
                    * new_pass ? - Password to be assigned to the user.
                * DELETE
                    * user - Username of the administrator.
                    * pass - Password of the administrator.
                    * name - Name of the client.
            * /{page}
                * GET
                    * user - Username of the administrator.
                    * pass - Password of the administrator.

