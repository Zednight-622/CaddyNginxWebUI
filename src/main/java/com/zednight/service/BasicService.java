package com.zednight.service;

import cn.craccd.sqlHelper.bean.Sort;
import cn.craccd.sqlHelper.bean.Sort.Direction;
import cn.craccd.sqlHelper.utils.ConditionOrWrapper;
import cn.craccd.sqlHelper.utils.SqlHelper;
import com.zednight.model.Basic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class BasicService {
    @Autowired
    SqlHelper sqlHelper;

    public Long buildOrder() {

        Basic basic = sqlHelper.findOneByQuery(new Sort().add(Basic::getSeq, Direction.DESC), Basic.class);
        if (basic != null) {
            return basic.getSeq() + 1;
        }

        return 0l;
    }

    @Transactional
    public void setSeq(String basicId, Integer seqAdd) {
        Basic basic = sqlHelper.findById(basicId, Basic.class);

        List<Basic> basicList = sqlHelper.findAll(new Sort(Basic::getSeq, Direction.ASC), Basic.class);
        if (basicList.size() > 0) {
            Basic tagert = null;
            if (seqAdd < 0) {
                for (Basic value : basicList) {
                    if (value.getSeq() < basic.getSeq()) {
                        tagert = value;
                    }
                }
            } else {
                for (int i = basicList.size() - 1; i >= 0; i--) {
                    if (basicList.get(i).getSeq() > basic.getSeq()) {
                        tagert = basicList.get(i);
                    }
                }
            }

            if (tagert != null) {
                // 交换seq
                Long seq = tagert.getSeq();
                tagert.setSeq(basic.getSeq());
                basic.setSeq(seq);

                sqlHelper.updateById(tagert);
                sqlHelper.updateById(basic);
            }

        }

    }

    public boolean contain(String content) {
        return sqlHelper.findCountByQuery(new ConditionOrWrapper().like(Basic::getValue, content).like(Basic::getName, content), Basic.class) > 0;
    }

}
