-- 构造一个有过期时间且限制大小的queue
-- 每次试图往该list插入新值时，都会先淘汰已过期的元素。并且只有当剩余元素小于max size时，才会插入新元素
-- 当在最新一个元素的过期时间内没有新元素插入时，整个queue都会过期
-- 返回剩余list size （插入新元素前）
-- KEYS[1] list key
-- ARGV[1] list max size
-- ARGV[2] timeout value
local list = KEYS[1]
local list_max_size = tonumber(ARGV[1])
local timeout = tonumber(ARGV[2])
local future_ts = 2000000000
redis.call('setnx', 'future', 1)
redis.call('expireat', 'future', future_ts)
local current_ts = future_ts - redis.call('ttl', 'future')
local llen = redis.call("LLEN", list)
for i = 1, llen do
    local liveTill = redis.call("LPOP", KEYS[1])
    if tonumber(liveTill) > tonumber(current_ts) then
        redis.call("RPUSH", list, liveTill)
    end
end
llen = redis.call("LLEN", list)
if llen < list_max_size then
    redis.call('RPUSH', list, timeout + current_ts)
    redis.call('PERSIST', list)
    redis.call('EXPIRE', list, timeout)
end
return llen