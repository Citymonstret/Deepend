/*
 * Copyright 2016 Minecade
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.minecade.deepend.client.test;
/*
 * Copyright 2016 Minecade
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import com.minecade.deepend.client.DeependClient;
import com.minecade.deepend.data.DeependBuf;
import com.minecade.deepend.game.GameCategory;
import com.minecade.deepend.game.GamePlayer;
import com.minecade.deepend.logging.Logger;
import com.minecade.deepend.object.ObjectManager;
import com.minecade.deepend.object.ProviderGroup;
import com.minecade.deepend.request.GetRequest;
import com.minecade.deepend.values.ValueFactory;

/**
 * Created 2/23/2016 for Deepend
 *
 * @author Citymonstret
 */
public class TestGameClient implements DeependClient.DeependClientApplication
{

    public static void main(String[] args)
    {
        new DeependClient( new TestGameClient() );

    }

    @Override
    public void registerInitialRequests(DeependClient client)
    {
        _registerInitialRequests( client );
    }

    private void _registerInitialRequests(DeependClient client)
    {
        client.addPendingRequest( new GetRequest( list -> {
            Logger.get().info( "Got a response with " + list.size() + " objects!" );
            list.forEach( System.out::println );
        } )
        {
            @Override
            public void buildRequest(DeependBuf buf)
            {
                buf.writeByte( GameCategory.SERVER_CATEGORIES );
                buf.writeString( "*" );
            }
        } );
    }

    @Override
    public void registerObjectMappings(ObjectManager objectManager)
    {
        // Will make sure that all requests using the category PLAYERS
        // returns a GamePlayer instance, rather than returning
        // raw data
        ObjectManager.instance.registerMapping( GamePlayer.class );
    }

    @Override
    public void registerFactories()
    {
        ValueFactory.addValueFactory( ValueFactory.FactoryType.DATA_TYPE, new ValueFactory<>( ProviderGroup.fromEnumClass( GameCategory.class ), GameCategory.UNKNOWN ) );
    }
}
