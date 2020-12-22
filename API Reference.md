* /api
    * /manage
        * /clients
            * /
                * GET
                    * Parameters
                        * user
                            * Username of the seller.
                        * pass
                            * Password of the seller.
                    * Description
                        * Returns client list's page count.
                * PUT
                    * Parameters
                        * user
                            * Username of the seller.
                        * pass
                            * Password of the seller.
                        * client
                            * Name of the client.
                        * is_company
                            * Is the new client a company.
                    * Description
                        * Adds a new client.
                * POST
                    * Parameters
                        * user
                            * Username of the seller.
                        * pass
                            * Password of the seller.
                        * client
                            * Name of the client.
                        * new_name
                            * Name to be assigned to the client.
                    * Description
                        * Assigns a new name to a given client.
                * DELETE
                    * Parameters
                        * user
                            * Username of the seller.
                        * pass
                            * Password of the seller.
                        * client
                            * Name of the client.
                    * Description
                        * Deletes a given client.
            * /{page}
                * GET
                    * Parameters
                        * user
                            * Username of the seller.
                        * pass
                            * Password of the seller.
                    * Description
                        * Returns the entries from the selected page of the client's list.
