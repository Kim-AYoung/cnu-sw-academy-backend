package org.prgms.kdt.order.voucher;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Repository
@Profile({"local", "default"})
public class MemoryVoucherRepository implements VoucherRepository/*, InitializingBean, DisposableBean*/ {

    private final Map<UUID, Voucher> storage = new ConcurrentHashMap<>();

    @Override
    public Optional<Voucher> findById(UUID voucherId) {
        return Optional.ofNullable(storage.get(voucherId));
    }

    @Override
    public Voucher insert(Voucher voucher) {
        storage.put(voucher.getVoucherId(), voucher);
        return voucher;
    }

//    @PostConstruct
//    public void postConstruct() {
//        System.out.println("[MemoryVoucherRepository] postConstruct called");
//    }
//
//    @Override
//    public void afterPropertiesSet() throws Exception {
//        System.out.println("[MemoryVoucherRepository] afterPropertiesSet called");
//    }
//
//    @PreDestroy
//    public void preDestory() { System.out.println("[MemoryVoucherRepository] preDestroy called"); }
//
//    @Override
//    public void destroy() throws Exception {
//        System.out.println("[MemoryVoucherRepository] destroy called");
//    }
}
