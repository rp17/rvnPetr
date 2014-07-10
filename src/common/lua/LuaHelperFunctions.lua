if(LuaHelperFunctions == nil) 
then 
    LuaHelperFunctions = {
        
        -- data must be LuaValue[] array, nil or 'primitive lua type'

        unpackUserData = function(self, data)
            if(data == nil) then return nil end
            if (type(data) ~= 'userdata') then return data end
            local t = {};
            local c = data.length;
            if(c == nil or c <= 0) then return t; end
            for i = 1,c do
                local vindex = 'V'..i
                if data[i]:isnumber() 
                then
                    t[vindex] = data[i]:todouble()
                elseif data[i]:isstring()
                then
                    t[vindex] = data[i]:tostring()
                elseif data[i]:isboolean()
                then
                    t[vindex] = data[i]:toboolean()
                elseif data[i]:isnil() 
                then
                    t[vindex] = nil;
                else
                    -- unsupported type
                    t[vindex] = data[i]
                end
            end
            return self.unpackObject(t,c,c);
        end,

        -- help function for unpackUserData
        -- recursive call of this function must be the LAST IN the RETURN statement
        -- otherwise it will not work properly

        unpackObject = function(t,len,i)
            if(i == nil or i < 1) then return nil end
            if(i == 1) then return t['V'..len] end
            return t['V'..(len-i+1)],LuaHelperFunctions.unpackObject(t,len,i-1)
        end
    };
end
