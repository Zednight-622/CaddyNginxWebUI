package com.zednight.service;

import cn.craccd.sqlHelper.bean.Sort;
import cn.craccd.sqlHelper.utils.ConditionOrWrapper;
import cn.craccd.sqlHelper.utils.SqlHelper;
import com.zednight.model.Global;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class GlobalService {
    @Autowired
    SqlHelper sqlHelper;

    public Long buildOrder() {

        Global global = sqlHelper.findOneByQuery(new Sort().add(Global::getSeq, Sort.Direction.DESC), Global.class);
        if (global != null) {
            return global.getSeq() + 1;
        }

        return 0L;
    }

    @Transactional
    public void setSeq(String globalId, Integer seqAdd) {
        Global global = sqlHelper.findById(globalId, Global.class);

        List<Global> GlobalList = sqlHelper.findAll(new Sort(Global::getSeq, Sort.Direction.ASC), Global.class);
        if (GlobalList.size() > 0) {
            Global tagert = null;
            if (seqAdd < 0) {
                for (Global g : GlobalList) {
                    if (g.getSeq() < g.getSeq()) {
                        tagert = g;
                    }
                }
            } else {
                for (int i = GlobalList.size() - 1; i >= 0; i--) {
                    if (GlobalList.get(i).getSeq() > global.getSeq()) {
                        tagert = GlobalList.get(i);
                    }
                }
            }

            if (tagert != null) {
                // 交换seq
                Long seq = tagert.getSeq();
                tagert.setSeq(global.getSeq());
                global.setSeq(seq);

                sqlHelper.updateById(tagert);
                sqlHelper.updateById(global);
            }

        }

    }

    public boolean contain(String content) {
        return sqlHelper.findCountByQuery(new ConditionOrWrapper().like(Global::getValue, content).like(Global::getName, content), Global.class) > 0;
    }

}
