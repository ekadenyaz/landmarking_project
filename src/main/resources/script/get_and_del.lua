local result = redis.call("get", KEYS[1])
if result then
    redis.call("del", KEYS[1])
    return result
end