#include <bits/stdc++.h>

#define INVERT(N) 1-N

using namespace std;

typedef long long DAMN_LONG;

class Crypto_node {
	public:
	DAMN_LONG *random_vars;
	list<Crypto_node *> ngb;
};

class Crypto_graph {
	
	protected:

	DAMN_LONG field_sz;
	int num_random_vars;
	
	public:

    DAMN_LONG extended_euclidean(DAMN_LONG a, DAMN_LONG b, DAMN_LONG *x, DAMN_LONG *y) {
        if(a==0) {
            *x = 0;
            *y = 1;
            return b;
        }
        DAMN_LONG x1, y1;
        DAMN_LONG gcd = extended_euclidean(b%a, a, &x1, &y1);

        *x = y1 - (b/a) * x1;
        *y = x1;

        return gcd;
    }

    DAMN_LONG multiplicative_inverse(DAMN_LONG num) {
        DAMN_LONG x, y;
        DAMN_LONG gcd = extended_euclidean(num, field_sz, &x, &y);
        if (gcd!=1)
            return -1;
        return (x % field_sz + field_sz) % field_sz;
    }

	DAMN_LONG field_plus(DAMN_LONG a, DAMN_LONG b) {
        return (a+b)%field_sz;
    }
	
	DAMN_LONG field_minus(DAMN_LONG a, DAMN_LONG b) {
        return (a-b+field_sz)%field_sz;
    }

    DAMN_LONG field_multiply(DAMN_LONG a, DAMN_LONG b) {
        return (a*b)%field_sz;
    }

    DAMN_LONG field_divide(DAMN_LONG a, DAMN_LONG b) {
        return (a * multiplicative_inverse(b))%field_sz;
    }
    
    void init_single_node(Crypto_node *n, DAMN_LONG val[]) {
    	for(int i=0;i<num_random_vars;i++)
    		n->random_vars[i] = val[i];
    }
    
    virtual void allocate_nodes_memory() = 0;
    virtual void init_all_nodes() = 0;
    
	void print_neighbours(Crypto_node *node) {
		
        cout << "Number of neighbours: " << node->ngb.size() << endl;
        list<Crypto_node *> :: iterator it;
        for(it = node->ngb.begin(); it!=node->ngb.end(); it++) {
        	cout << "(" << (*it)->random_vars[0];
        	for(int i=1;i<num_random_vars;i++)
            	cout << ", " << (*it)->random_vars[i];
            cout << ")" << endl;
		}
	}
    
    Crypto_graph(DAMN_LONG f_sz, int n_rand_vars) {
        field_sz = f_sz;
        num_random_vars = n_rand_vars;
	}
};
