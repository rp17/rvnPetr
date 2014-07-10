class = function(name,...)
    local mt = {
            __call = function(self,...) 
                        self:__init(...)
                        return self
            end,
            __newindex = function (self,k,v)
                -- create my own __init implementation, 
                -- witch provide 'super' function for original __init
                if(k == '__init') then                     
                    rawset(self,k, function(...)
                                        local tmp = _G['super']
                                        _G['super'] = function(...)
                                                local mt = getmetatable(self)
                                                mt.__index.__init(self,...)
                                        end
                                        -- call original __init function
                                        v(...)
                                        _G['super'] = tmp
                                    end);
                 else
                    rawset(self,k, v); 
                 end
            end
    };

    local classMT = {
        __index = function(self,method)
            local javaMethodName = _G[self.luaname].getMethod(method) 
            if not javaMethodName
            then 
                error('Unknown method '..method..', try .def("methodName") in Java') 
            end
            return function(self,...)
                return self.instance[javaMethodName](self.instance,...)
            end
        end
    };

    local classNewMT = {
        __call = function(self,...)
            return setmetatable({luaname=self.luaname,instance = luajava.newInstance(self.class,...)},
                    classMT)
        end
    };

    function addClass(cname,newname)        
        local methods = {}
        local base = nil
	local fuj = newname
        _G[newname] = setmetatable({
                        class=cname,
                        luaname=newname,
                        setBase = function(luaClassName)
                            base = luaClassName
                        end,
                        setMethod = function(luaMethodName,javaMethodName)
                            methods[luaMethodName] = javaMethodName
                        end,
                        getMethod = function(method)
                            local m = methods[method]
                            if m then return m end
                            if base then
                                return _G[base].getMethod(method)
                            end
                            return nil
                        end,
			getMetaTableForUserdata = function()
			    return {
				__index = function(self,method)
					local c = luajava.bindClass(cname)
					if c[method]
					then
						if not _G[newname].getMethod(method)
						then
							error("Method "..method.." is not allowed in "..newname)
						end
						return c[method]
					end
					return rawget(self,method)
				end
			    }
			end
                    },classNewMT)
    end

    function addMethod(name)    
        _G[name] = setmetatable({},mt)

        return function (BaseClass)
            local x = _G[name]
            local mt = getmetatable(x) or {}
            mt.__index = BaseClass
            return setmetatable(x,mt)
        end
    end

    if(name == '@') 
    then
        cname,newname = unpack({...})
        addClass(cname,newname)
    else
        return addMethod(name)
    end
end