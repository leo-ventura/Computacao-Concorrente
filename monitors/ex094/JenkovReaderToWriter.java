public class JenkovReaderToWriter{

    private Map<Thread, Integer> readingThreads =                               //como e possivel observar, e criado um mapa
        new HashMap<Thread, Integer>();                                         //para verificar se uma thread possui ou nao acesso ao lock.

    private int writeAccesses    = 0;
    private int writeRequests    = 0;
    private Thread writingThread = null;

  public synchronized void lockWrite() throws InterruptedException{
    writeRequests++;
    Thread callingThread = Thread.currentThread();
    while(! canGrantWriteAccess(callingThread)){
      wait();
    }
    writeRequests--;
    writeAccesses++;
    writingThread = callingThread;
  }

  public synchronized void unlockWrite() throws InterruptedException{
    writeAccesses--;
    if(writeAccesses == 0){
      writingThread = null;
    }
    notifyAll();
  }

  private boolean canGrantWriteAccess(Thread callingThread){
    if(isOnlyReader(callingThread))    return true;                             //Chama o metodo responsavel pela observacao do map, a fim de garantir as circunstancias da reentrancia
    if(hasReaders())                   return false;
    if(writingThread == null)          return true;
    if(!isWriter(callingThread))       return false;
    return true;
  }

  private boolean hasReaders(){
    return readingThreads.size() > 0;
  }

  private boolean isWriter(Thread callingThread){
    return writingThread == callingThread;
  }

  private boolean isOnlyReader(Thread thread){
      return readers == 1 && readingThreads.get(callingThread) != null;         //Aqui e feita a verificacao no mapa que torna possivel essa rotina
      }

}
