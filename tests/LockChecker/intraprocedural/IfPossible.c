typedef union {
  char __size[24];
  long int __align;
} spinlock_t;

extern spinlock_t lock_l;
int a,b,c;

int main( int argc, char *argv[]  ) {
	a = 0;	
	spin_lock(lock_l);
	if(b==1) // lock
	{		
		a = 1;	// lock
		spin_unlock(lock_l); // lock
	}	
	a = 2; // lock | no lock
	spin_unlock(lock_l); // lock | no lock
    return 0; // no lock
}
