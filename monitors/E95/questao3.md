```java
    void transfer(int k, Account reserve) {
        lock.lock();
        try {
            reserve.withdraw(k);
            deposit(k);
        } finally {lock.unlock();}
    }
```

> We are given a set of 10 accounts, whose balances are unknown. At 1:00, each
> of n threads tries to transfer $100 from another account into its own account.
> At 2:00, a Boss thread deposits $1000 to each account. Is every transfer method
> called at 1:00 certain to return?

***

_Resposta:_ <br>
**Não**, não é certo de retornar. Com a implementação que fizemos não podemos ter certeza que cada operação irá retornar.
Isso se dá pelo fato de que uma thread poderá ficar em espera infinita se a conta que ela está esperando ter crédito
nunca receber dinheiro suficiente. 
Podemos confirmar o pensamento se levarmos em conta o "pior caso":
<ul>
    <li> todas as contas tentam transferir saindo de apenas uma conta (vamos chamá-la de conta1, começando com saldo 0) para as outras, o débito máximo que se chegará será 100 * n (quantidade de threads)</li>
    <li> assim, se tivermos um valor de n <= 10, tudo funcionaria bem, pois mesmo que tentássemos retirar somente da conta1, ao receber 1000 da thread Boss, o débito seria realizado e a operação retornaria. </li>
    <li> entretanto, se houver um número de threads > 10, a conta1 ficará devendo eternamente, visto que não seria possível abater o valor.</li>
</ul>