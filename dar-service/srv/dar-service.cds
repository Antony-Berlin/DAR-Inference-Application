service DarService {


    entity features {
        key ID     : UUID;
            parent : Association to objects;
            name   : String(40);
            value  : String(40);
    }

    entity objects {
        key ID : UUID;
        parent : Association to Root;
        features : Composition of many features on features.parent = $self;
    }

    // @cds.persistence.skip
    entity Root {

        key ID  : UUID;
        topN : Integer;
        objects : Composition of many  objects on objects.parent = $self;
        Response        : Association to Response on Response.parent = $self;
    }

    // @cds.persistence.skip
    entity Response {

        key ID      : UUID;
        parent       : Association to Root;
        responseText : String;
    }



   

  
}
