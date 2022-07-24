package org.prgms.kdt.order.voucher;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
@Qualifier("jdbc")
public class JDBCVoucherRepository implements VoucherRepository {
    @Override
    public Optional<Voucher> findById(UUID voucherId) {
        return Optional.empty();
    }

    @Override
    public Voucher insert(Voucher voucher) {
        return null;
    }
}
